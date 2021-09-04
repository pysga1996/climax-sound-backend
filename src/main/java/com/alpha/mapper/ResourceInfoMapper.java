package com.alpha.mapper;

/**
 * @author thanhvt
 * @created 06/06/2021 - 5:39 CH
 * @project vengeance
 * @since 1.0
 **/

import com.alpha.model.dto.ResourceInfoDTO;
import com.alpha.model.entity.ResourceInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR)
public abstract class ResourceInfoMapper {

    public abstract ResourceInfoDTO entityToDto(ResourceInfo resourceInfo);

    public abstract ResourceInfo dtoToEntity(ResourceInfoDTO resourceInfo);

    public abstract List<ResourceInfoDTO> entityToDtoList(List<ResourceInfo> resourceInfos);

    public abstract List<ResourceInfo> dtoToEntityList(List<ResourceInfoDTO> resourceInfos);
}
