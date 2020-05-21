package me.samarthsinha.postgrest4j.models.rules;

import java.util.List;
import java.util.Map;

public class ConditionalBlock {
    private ConditionValue condition;
    private List<ConditionalBlock> rules;
    private String field;
    private OperatorValues operator;
    private String fieldType;
    private String value;

    public ConditionValue getCondition() {
        return condition;
    }

    public void setCondition(ConditionValue condition) {
        this.condition = condition;
    }

    public List<ConditionalBlock> getRules() {
        return rules;
    }

    public void setRules(List<ConditionalBlock> rules) {
        this.rules = rules;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public OperatorValues getOperator() {
        return operator;
    }

    public void setOperator(OperatorValues operator) {
        this.operator = operator;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean evaluate(Map<String,Object> stringObjectMap){
        if(this.condition!=null){
            boolean result = false;
            if(this.condition == ConditionValue.and){
                result = true;
                for(ConditionalBlock conditionalBlock: this.rules){
                    result = result && conditionalBlock.evaluate(stringObjectMap);
                    if(!result) return false;
                }
            }else if(this.condition == ConditionValue.or){
                for(ConditionalBlock conditionalBlock: this.rules){
                    result = result || conditionalBlock.evaluate(stringObjectMap);
                    if(result) return true;
                }
            }
            return false;
        }else{
            String field = this.getField();
            Object rightValue = null;
            if(stringObjectMap!=null){
                rightValue = stringObjectMap.getOrDefault(field,null);
            }
            return this.operator.operate(rightValue,this.value);
        }
    }

}
