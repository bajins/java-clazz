package com.bajins.clazz;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义进制类
 */
public class CustomDigit {

    /**
     * 十进制数
     */
    private BigDecimal decimal;

    /**
     * 用于储存转换后数据的列表
     */
    private List<CustomInteger> numberList = new ArrayList<>();

    /**
     * 位数，比如2、8、10、16、60等等任意正整数
     */
    private Integer digit;

    /**
     * 最小值，默认为0
     */
    private Integer minValue = 0;

    public CustomDigit() {
        super();
    }

    public CustomDigit(BigDecimal decimal, Integer digit) {
        this.decimal = decimal;
        this.digit = digit;
        numberList.add(new CustomInteger(0));
    }

    public CustomDigit(String decimal, Integer digit) {
        this.decimal = new BigDecimal(decimal);
        this.digit = digit;
        numberList.add(new CustomInteger(0));
    }

    public CustomDigit(BigDecimal decimal, Integer digit, Integer minValue) {
        this.decimal = decimal;
        this.digit = digit;
        this.minValue = minValue;
        numberList.add(new CustomInteger(0));
    }

    public CustomDigit(String decimal, Integer digit, Integer minValue) {
        this.decimal = new BigDecimal(decimal);
        this.digit = digit;
        this.minValue = minValue;
        numberList.add(new CustomInteger(0));
    }

    /**
     * @return the decimal
     */
    public BigDecimal getDecimal() {
        return decimal;
    }

    /**
     * @param decimal the decimal to set
     */
    public void setDecimal(BigDecimal decimal) {
        this.decimal = decimal;
    }

    /**
     * @return the numberList
     */
    public List<CustomInteger> getNumberList() {
        return numberList;
    }

    /**
     * @param numberList the numberList to set
     */
    public void setNumberList(List<CustomInteger> numberList) {
        this.numberList = numberList;
    }

    /**
     * @return the digit
     */
    public Integer getDigit() {
        return digit;
    }

    /**
     * @param digit the digit to set
     */
    public void setDigit(Integer digit) {
        this.digit = digit;
    }

    /**
     * @return the minValue
     */
    public Integer getMinValue() {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public void clear() {
        this.decimal = BigDecimal.ZERO;
        this.digit = 0;
        this.minValue = 0;
        this.numberList.clear();
    }

    public Integer getLength() {
        return this.numberList.size();
    }

    private List<CustomInteger> formattedNumberList() {
        int initialNumber = decimal.intValue();
        while (initialNumber > 0) {
            int tempNumber = initialNumber > digit ? digit : initialNumber;
            CustomInteger lastNumber = this.numberList.get(getLength() - 1);
            lastNumber.setValue(lastNumber.getValue() + tempNumber);
            initialNumber = initialNumber - tempNumber;
            arrangementList(getLength() - 1);
        }

        return this.numberList;
    }

    private void arrangementList(Integer index) {
        CustomInteger lastNumber = this.numberList.get(index);
        if (lastNumber.getValue() >= digit) {
            boolean flag = false;
            int value = lastNumber.getValue() - digit + minValue;
            if (value >= digit) {
                flag = true;
                // 防止+minValue之后又再次大于进制的阀值
                value = value - digit + minValue;
            }
            // 如果再次大于阀值，则向上一位进2，否则进1
            int count = flag ? 2 : 1;
            lastNumber.setValue(value);
            if (index == 0) {
                this.numberList.add(0, new CustomInteger(count));
                return;
            } else {
                CustomInteger preNumber = this.numberList.get(index - 1);
                preNumber.setValue(preNumber.getValue() + count);
            }
        }
        if (index == 0) {
            return;
        }
        arrangementList(index - 1);
    }

    public List<Integer> getFormattedNumberList() {
        List<Integer> list = new ArrayList<Integer>();
        this.formattedNumberList();
        for (CustomInteger ci : this.numberList) {
            list.add(ci.getValue());
        }
        return list;
    }
}

class CustomInteger {
    private Integer value;

    public CustomInteger() {
    }

    public CustomInteger(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(value);
    }
}