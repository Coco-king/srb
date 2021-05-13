package top.codecrab.srb.core.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * 等额本金工具类
 * 校验网址：http://www.xjumc.com/
 * 等额本金是指一种贷款的还款方式，是在还款期内把贷款数总额等分，每月偿还同等数额的本金和剩余贷款在该月所产生的利息，这样由于每月的还款本金额固定，
 * 而利息越来越少，借款人起初还款压力较大，但是随时间的推移每月还款数也越来越少。
 *
 * @author codecrab
 */
public class Amount2Helper {

    private static final BigDecimal TWELVE = new BigDecimal("12");

    /**
     * 每月本息
     *
     * @param invest     总借款额（贷款本金）
     * @param yearRate   年利率
     * @param totalMonth 还款总月数
     * @return 每月偿还利息
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipalInterest(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(totalMonth, 1);
        // 每月本金
        BigDecimal monthPri = invest.divide(new BigDecimal(totalMonth), 8, BigDecimal.ROUND_DOWN);
        // 获取月利率
        double monthRate = yearRate.divide(TWELVE, 8, BigDecimal.ROUND_DOWN).doubleValue();
        monthRate = new BigDecimal(monthRate).setScale(8, BigDecimal.ROUND_DOWN).doubleValue();
        for (int i = 1; i <= totalMonth; i++) {
            double monthRes = monthPri.doubleValue() + (invest.doubleValue() - monthPri.doubleValue() * (i - 1)) * monthRate;
            monthRes = new BigDecimal(monthRes).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            map.put(i, new BigDecimal(monthRes));
        }
        return map;
    }

    /**
     * 每月还款利息
     */
    public static Map<Integer, BigDecimal> getPerMonthInterest(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        Map<Integer, BigDecimal> inMap = new HashMap<>(totalMonth, 1);
        BigDecimal principal = invest.divide(new BigDecimal(totalMonth), 8, BigDecimal.ROUND_DOWN);
        Map<Integer, BigDecimal> map = getPerMonthPrincipalInterest(invest, yearRate, totalMonth);
        map.forEach((k, v) -> {
            BigDecimal principalInterestBigDecimal = new BigDecimal(v.toString());
            BigDecimal interestBigDecimal = principalInterestBigDecimal.subtract(principal);
            interestBigDecimal = interestBigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
            inMap.put(k, interestBigDecimal);
        });
        return inMap;
    }

    /**
     * 每月还款本金
     *
     * @param invest     总借款额（贷款本金）
     * @param totalMonth 还款总月数
     * @return 总利息
     */
    public static Map<Integer, BigDecimal> getPerMonthPrincipal(BigDecimal invest, int totalMonth) {
        Map<Integer, BigDecimal> map = new HashMap<>(totalMonth, 1);
        BigDecimal monthIncome = invest.divide(new BigDecimal(totalMonth), 8, BigDecimal.ROUND_DOWN);
        for (int i = 1; i <= totalMonth; i++) {
            map.put(i, monthIncome);
        }
        return map;
    }

    /**
     * 总利息
     */
    public static BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, int totalMonth) {
        BigDecimal count = BigDecimal.ZERO;
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, totalMonth);

        for (Map.Entry<Integer, BigDecimal> entry : mapInterest.entrySet()) {
            count = count.add(entry.getValue());
        }
        return count;
    }

    public static void main(String[] args) {
        // 本金
        BigDecimal invest = new BigDecimal("12000");
        int month = 12;
        // 年利率
        BigDecimal yearRate = new BigDecimal("0.12");

        Map<Integer, BigDecimal> benJin = getPerMonthPrincipal(invest, month);
        System.out.println("等额本金---每月本金:" + benJin);
        Map<Integer, BigDecimal> mapInterest = getPerMonthInterest(invest, yearRate, month);
        System.out.println("等额本金---每月利息:" + mapInterest);
        BigDecimal count = getInterestCount(invest, yearRate, month);
        System.out.println("等额本金---总利息：" + count);
    }
}
