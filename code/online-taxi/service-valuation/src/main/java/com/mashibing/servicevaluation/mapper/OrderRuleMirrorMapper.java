package com.mashibing.servicevaluation.mapper;

//
import com.mashibing.servicevaluation.entity.OrderRuleMirror;

public interface OrderRuleMirrorMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    int insert(OrderRuleMirror record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    int insertSelective(OrderRuleMirror record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    OrderRuleMirror selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(OrderRuleMirror record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(OrderRuleMirror record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tbl_order_rule_mirror
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(OrderRuleMirror record);

    /**
     * This method corresponds to the database table tbl_order
     */
    OrderRuleMirror selectByOrderId(Long orderId);
}