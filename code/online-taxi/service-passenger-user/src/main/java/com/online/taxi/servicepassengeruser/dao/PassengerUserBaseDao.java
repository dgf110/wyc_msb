package com.online.taxi.servicepassengeruser.dao;

import com.online.taxi.servicepassengeruser.entity.PassengerUserBase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PassengerUserBaseDao {

    int deleteByPrimaryKey(Integer id);

    int insert(PassengerUserBase record);

    int insertSelective(PassengerUserBase record);

    PassengerUserBase selectByPrimaryKey(Integer id);

    PassengerUserBase selectByPhone(String phone);

    int updateByPrimaryKeySelective(PassengerUserBase record);

    int updateByPrimaryKey(PassengerUserBase record);
}