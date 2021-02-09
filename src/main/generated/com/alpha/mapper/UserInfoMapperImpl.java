package com.alpha.mapper;

import com.alpha.model.dto.UserInfoDTO;
import com.alpha.model.entity.UserInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-02-11T00:30:21+0700",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_261 (Oracle Corporation)"
)
@Component
public class UserInfoMapperImpl extends UserInfoMapper {

    @Override
    public UserInfoDTO entityToDto(UserInfo userInfo) {
        if ( userInfo == null ) {
            return null;
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();

        userInfoDTO.setId( userInfo.getId() );
        userInfoDTO.setInfo( userInfo.getInfo() );

        return userInfoDTO;
    }

    @Override
    public UserInfo dtoToEntity(UserInfoDTO userInfo) {
        if ( userInfo == null ) {
            return null;
        }

        UserInfo userInfo1 = new UserInfo();

        userInfo1.setId( userInfo.getId() );
        userInfo1.setInfo( userInfo.getInfo() );

        return userInfo1;
    }

    @Override
    public List<UserInfoDTO> entityToDtoList(List<UserInfo> userInfos) {
        if ( userInfos == null ) {
            return null;
        }

        List<UserInfoDTO> list = new ArrayList<UserInfoDTO>( userInfos.size() );
        for ( UserInfo userInfo : userInfos ) {
            list.add( entityToDto( userInfo ) );
        }

        return list;
    }

    @Override
    public List<UserInfo> dtoToEntityList(List<UserInfoDTO> userInfos) {
        if ( userInfos == null ) {
            return null;
        }

        List<UserInfo> list = new ArrayList<UserInfo>( userInfos.size() );
        for ( UserInfoDTO userInfoDTO : userInfos ) {
            list.add( dtoToEntity( userInfoDTO ) );
        }

        return list;
    }
}
