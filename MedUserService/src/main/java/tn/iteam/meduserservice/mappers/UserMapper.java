package tn.iteam.meduserservice.mappers;

import org.mapstruct.Mapper;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.models.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toUserResponseDto(UserEntity entity);
}
