package top.codecrab.srb.core.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 一次还本还息工具类
 *
 * @author codecrab
 */
public class Amount4Helper {

    private static final BigDecimal TWELVE = new BigDecimal("12");

    /**
     * 总利息
     */
    public static BigDecimal getInterestCount(BigDecimal amount, BigDecimal yearRate, int totalMonth) {
        BigDecimal monthInterest = yearRate.divide(TWELVE, 8, BigDecimal.ROUND_HALF_UP);
        return amount.multiply(monthInterest).multiply(new BigDecimal(totalMonth)).divide(BigDecimal.ONE, 8, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 还款金额 = 本金 + 本金 * 月利率 * 期限
     *
     * @param amount     本金
     * @param yearRate   年利率
     * @param totalMonth 期限
     * @return 还款金额
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal amount, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(1, 1);
        BigDecimal multiply = getInterestCount(amount, yearRate, totalMonth);
        map.put(1, multiply);
        return map;
    }

    /**
     * 还款本金
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal amount) {
        Map<Integer, BigDecimal> map = new HashMap<>(1, 1);
        map.put(1, amount);
        return map;
    }

    /**
     * 还款本金 + 总利息
     */
    public static BigDecimal getInterestCountWithPrincipal(BigDecimal amount, BigDecimal yearRate, int totalMonth) {
        return amount.add(getInterestCount(amount, yearRate, totalMonth));
    }


    public static void main(String[] args) {
        // 本金
        BigDecimal invest = new BigDecimal("12000");
        int month = 12;
        // 年利率
        BigDecimal yearRate = new BigDecimal("0.12");

        Map<Integer, BigDecimal> getPerMonthPrincipalInterest = getPerMonthPrincipal(invest);
        System.out.println("一次还本还息---偿还本金：" + getPerMonthPrincipalInterest);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, month);
        System.out.println("一次还本还息---总利息：" + mapInterest);
        BigDecimal count = getInterestCount(invest, yearRate, month);
        System.out.println("一次还本还息---总利息：" + count);
        BigDecimal count1 = getInterestCountWithPrincipal(invest, yearRate, month);
        System.out.println("一次还本还息---还款本金+总利息：" + count1);
    }
}
