package me.samarthsinha.postgrest4j.models.rules;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum  OperatorValues {
    EQUALS("="){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            if(leftValue==null){
                return rightValue==null;
            }
            if(leftValue instanceof String){
                return leftValue.equals(rightValue);
            } else if(leftValue instanceof Number){
                return new BigDecimal(((Number) leftValue).toString()).compareTo(new BigDecimal(rightValue))==0;
            } else {
                return Objects.equals(leftValue,rightValue);
            }
        }
    },
    LT("<"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            if(leftValue==null){
                return rightValue!=null;
            }
            if(leftValue instanceof Number){
                BigDecimal actualNum = new BigDecimal(leftValue.toString());
                BigDecimal thresVal = new BigDecimal(rightValue);
                return actualNum.compareTo(thresVal) < 0;
            } else if(leftValue instanceof String){
                return ((String) leftValue).compareTo(rightValue) < 0;
            }
            return false;
        }
    },
    GT(">"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            if(rightValue == null){
                return leftValue!=null;
            }
            if(leftValue instanceof Number){
                BigDecimal actualNum = new BigDecimal(leftValue.toString());
                BigDecimal thresVal = new BigDecimal(rightValue);
                return actualNum.compareTo(thresVal) > 0;
            } else if(leftValue instanceof String){
                return ((String) leftValue).compareTo(rightValue) > 0;
            }
            return false;
        }
    },
    LTE("<="){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            return LT.operate(leftValue,rightValue) || EQUALS.operate(leftValue,rightValue);
        }
    },
    GTE(">="){

        @Override
        boolean operate(Object leftValue, String rightValue) {
            return GT.operate(leftValue,rightValue) || EQUALS.operate(leftValue,rightValue);
        }
    },
    CONTAINS("contains"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            if(leftValue==null){
                return false;
            }
            if(leftValue instanceof String){
                return ((String) leftValue).contains(rightValue);
            }
            return false;
        }
    },
    EQUALSIGNORECASE("equalsIgnoreCase"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            return false;
        }
    },
    NOT_EQUALS("!="){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            return !EQUALS.operate(leftValue,rightValue);
        }
    },
    IS_NULL("is null"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            return Objects.isNull(leftValue);
        }
    },

    IS_NOT_NULL("is not null"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            return Objects.nonNull(leftValue);
        }
    },
    IN("in"){
        @Override
        boolean operate(Object leftValue, String rightValue) {
            return false;
        }
    },
    NOT_IN("not_in"){

        @Override
        boolean operate(Object leftValue, String rightValue) {
            return false;
        }
    };

    static Map<String,OperatorValues> displayToOperatorValue;

    static{
        displayToOperatorValue = new HashMap<>();
        for(OperatorValues operatorValues: OperatorValues.values()){
            displayToOperatorValue.put(operatorValues.displayValue,operatorValues);
        }
    }

    abstract boolean operate(Object leftValue, String rightValue);

    String displayValue;

    OperatorValues(String displayValue) {
        this.displayValue = displayValue;
    }

    static OperatorValues getOperatorValue(String op){
        return displayToOperatorValue.get(op);
    }


}
