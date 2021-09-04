package com.alpha.mapper;

import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.UserInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR)
public abstract class UserInfoMapper {

    public abstract UserInfoDTO entityToDto(UserInfo userInfo);

    public abstract UserInfo dtoToEntity(UserInfoDTO userInfo);

    public abstract List<UserInfoDTO> entityToDtoList(List<UserInfo> userInfos);

    public abstract List<UserInfo> dtoToEntityList(List<UserInfoDTO> userInfos);
}
