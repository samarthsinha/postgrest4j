package me.samarthsinha.postgrest4j.services;

import me.samarthsinha.postgrest4j.repositories.MasterConfigurationRepo;
import me.samarthsinha.postgrest4j.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostgresDbService implements DbServices {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDbService.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    MasterConfigurationRepo masterConfigurationRepo;

    @Value("${spring.datasource.url}")
    String url;

    @PostConstruct
    public void init(){
        LOGGER.info("DATABASE URL::"+url);
    }

    @Transactional
    @Override
    public Object createUnderlyingTableForMaster(ConfigFilter configFilter) throws Exception {
        String masterCateogryName = configFilter.getMasterName();
        String tenantId = configFilter.getTenantId();
        MasterConfigurations masterConfigurations = masterConfigurationRepo.findFirstByTenantIdAndMasterName(tenantId, masterCateogryName);
        if (masterConfigurations == null) {
            return "No such master data is defined, to be published";
        }
        String tableName = masterConfigurations.getTableName();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE public.")
                .append(tableName).append("(");
        List<ColumnDefinition> columnsDetails = masterConfigurations.getColumnsDetails();
        columnsDetails.forEach(c -> queryBuilder.append(c.getTableColumnName()).append(" ").append(UsertypeToDatabaseTypeMapping.valueOf(c.getDataType()).getPgDataType()).append(","));
        queryBuilder.append(" _created_at TIMESTAMP")
                .append(",_update_at TIMESTAMP")
                .append(",_update_by varchar(255)");
        queryBuilder.append(")");
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.prepareStatement(queryBuilder.toString())) {
            int i = ((PreparedStatement) statement).executeUpdate();
            if(i==0){
                masterConfigurationRepo.activateMasterConfig(masterConfigurations.getId());
            }
            return i == 0 ? "Created successfully" : "Error occurred";
        } catch (Exception e) {
            LOGGER.error("Error in creating underlying master table: ", e);
            return e;
        }
    }

    @Override
    public Object createMasterDataDefinition(MasterConfigurations masterConfigurations) throws Exception {
        List<ColumnDefinition> columnsDetails = masterConfigurations.getColumnsDetails();
        if (columnsDetails != null) {
            List<ColumnDefinition> collect = columnsDetails.stream().map(c -> {
                String columnName = c.getColumnName() != null ? c.getColumnName().toLowerCase() : "undefined_col";
                String s = columnName.replaceAll("[^a-zA-Z0-9]", " ")
                        .trim()
                        .replaceAll("\\s+", "_");
                c.setTableColumnName(s);
                return c;
            }).collect(Collectors.toList());
            String masterName = masterConfigurations.getMasterName();
            masterConfigurations.setColumnsDetails(collect);
            masterName = "master " + masterConfigurations.getTenantId() + " " + masterName;
            String s = masterName.toLowerCase()
                    .replaceAll("[^a-zA-Z0-9]", " ")
                    .trim().replaceAll("\\s+", "_");
            masterConfigurations.setTableName(s);
            MasterConfigurations save = masterConfigurationRepo.save(masterConfigurations);
            return save;
        }
        return null;
    }

    @Override
    public Object insertData(InsertDto insertDto) throws Exception {
        ConfigFilter configFilter = insertDto.getConfigFilter();
        String masterCateogry = configFilter.getMasterName();
        String tenantId = configFilter.getTenantId();
        List<Map<String,Object>> data = insertDto.getData();
        try(Connection connection = dataSource.getConnection()) {
            MasterConfigurations masterConfigurations = masterConfigurationRepo.findFirstByTenantIdAndMasterName(tenantId, masterCateogry);
            if (masterConfigurations == null) {
                return "No such master data is defined";
            }

            connection.setAutoCommit(false);
            StringBuilder insertStatement = new StringBuilder();
            insertStatement.append("INSERT INTO public.").append(masterConfigurations.getTableName()).append(" (_created_at, _update_at, _update_by ");

            List<ColumnDefinition> columnsDetails = masterConfigurations.getColumnsDetails();
            Map<String, ColumnDefinition> columnDefinitionMap = new LinkedHashMap<>();
            columnsDetails.forEach(columnDefinition -> {
                columnDefinitionMap.put(columnDefinition.getColumnName(), columnDefinition);
            });

            List<String> keys = new ArrayList<>();
            data.get(0).forEach((k, v) -> {
                keys.add(k);
                insertStatement.append(",").append(columnDefinitionMap.get(k).getTableColumnName());
            });
            insertStatement.append(")");
            insertStatement.append(" VALUES(?,?,?");
            for (int i = 0; i < keys.size(); i++) {
                insertStatement.append(",?");
            }
            insertStatement.append(")");
            System.out.println(insertStatement.toString());
            try (PreparedStatement pstmt = connection.prepareStatement(insertStatement.toString())) {
                for (Map<String, Object> d : data) {
                    pstmt.setObject(1, new Timestamp(System.currentTimeMillis()));
                    pstmt.setObject(2, new Timestamp(System.currentTimeMillis()));
                    pstmt.setString(3, "user");
                    for (int i = 0; i < keys.size(); i++) {
                        pstmt.setObject(
                                4 + i, d.get(keys.get(i)));
                    }
                    pstmt.addBatch();

                }
                int[] ints;
                try {
                    // Batch is ready, execute it to insert the data

                    ints = pstmt.executeBatch();
                } catch (SQLException e) {
                    System.out.println("Error message: " + e.getMessage());
                    return "Error " + e.getMessage(); // Exit if there was an error
                }
                connection.commit();
                return ints;
            }
        } catch (Exception e) {
            return e;
        }
    }

    @Override
    public Object filterData(QueryFilter queryFilter) throws Exception {
        try {
            Map<String, List<Object>> data = queryFilter.getDataFilter();
            String tenantId = queryFilter.getConfigFilter().getTenantId();
            String masterCateogry = queryFilter.getConfigFilter().getMasterName();
            MasterConfigurations masterConfigurations = masterConfigurationRepo.findFirstByTenantIdAndMasterName(tenantId, masterCateogry);
            if (masterConfigurations == null) {
                return "No such master data is defined";
            }
            Map<String, ColumnDefinition> columnDefinitionMap = new LinkedHashMap<>();
            StringBuilder queryBuilder = getQuery(masterConfigurations, columnDefinitionMap);

            Connection connection = dataSource.getConnection();

            List<String> keynames = new ArrayList<>();
            int size = data.size();
            int count[] = {0};
            int parmCnt[] = {1};
            if(size>0){
                queryBuilder.append(" where ");
            }
            data.forEach((k, v) -> {
                ColumnDefinition columnDefinition = columnDefinitionMap.get(k);
                queryBuilder.append(columnDefinition.getTableColumnName()).append(" ").append("in");
                int cnt = 0;
                int size1 = v.size();

                for (Object o : v) {
                    if (cnt == 0) queryBuilder.append("(");
                    queryBuilder.append("?").append("::").append(UsertypeToDatabaseTypeMapping.valueOf(columnDefinition.getDataType()).getPgCastType());
                    cnt++;
                    if (cnt < size1) {
                        queryBuilder.append(",");
                    }
                    if (cnt == size1) {
                        queryBuilder.append(")");
                    }
                }
                count[0]++;
                if (count[0] < size) {
                    queryBuilder.append(" AND ");
                }
                keynames.add(k);
            });
            int i = 1;
            System.out.println(queryBuilder.toString());
            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            for (int j = 0; j < keynames.size(); j++) {
                List<Object> objects = data.get(keynames.get(j));
                for (Object o : objects) {
                    preparedStatement.setObject(i++, o);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> result = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int ks = 1; ks <= columnCount; ks++) {
                    map.put(metaData.getColumnName(ks), resultSet.getObject(ks));
                }
                result.add(map);
            }
            Map<String, Object> re = new LinkedHashMap<>();
            re.put("result", result);
            re.put("metadata", metaData);
            return re;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private StringBuilder getQuery(MasterConfigurations masterConfigurations, Map<String, ColumnDefinition> columnDefinitionMap) {
        List<ColumnDefinition> columnsDetails = masterConfigurations.getColumnsDetails();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ");
        int definedColCount[] = {0};
        columnsDetails.forEach(columnDefinition -> {
            columnDefinitionMap.put(columnDefinition.getColumnName(), columnDefinition);
            queryBuilder.append(columnDefinition.getTableColumnName()).append(" AS \"").append(columnDefinition.getColumnName()).append("\"");
            definedColCount[0]++;
            if (definedColCount[0] < columnsDetails.size()) {
                queryBuilder.append(" , ");
            }
        });
        queryBuilder.append(" from ").append(masterConfigurations.getTableName());
        return queryBuilder;
    }
}
