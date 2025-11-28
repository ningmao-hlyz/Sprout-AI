package top.ningmao.myspring.ai.chat.client;

import top.ningmao.myspring.ai.chat.advisor.Advisor;

import java.util.List;
import java.util.Map;


/**
 * AdvisorSpec 接口
 * 用于在运行时配置 Advisor 参数
 *
 * @author 宁猫
 * @since 2025-11-28 15:48:23
 */
public interface AdvisorSpec {

    /**
     * 添加单个参数
     *
     * @param key   参数键
     * @param value 参数值
     * @return AdvisorSpec
     */
    AdvisorSpec param(String key, Object value);

    /**
     * 批量添加参数
     *
     * @param params 参数 Map
     * @return AdvisorSpec
     */
    AdvisorSpec params(Map<String, Object> params);

    /**
     * 添加运行时 Advisors（覆盖默认）
     *
     * @param advisors Advisor 数组
     * @return AdvisorSpec
     */
    AdvisorSpec advisors(Advisor... advisors);

    /**
     * 添加运行时 Advisors（覆盖默认）
     *
     * @param advisors Advisor 列表
     * @return AdvisorSpec
     */
    AdvisorSpec advisors(List<Advisor> advisors);
}
