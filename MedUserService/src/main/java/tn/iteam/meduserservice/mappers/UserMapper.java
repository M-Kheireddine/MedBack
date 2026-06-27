package tn.iteam.meduserservice.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import tn.iteam.meduserservice.dtos.responses.DoctorResponseDto;
import tn.iteam.meduserservice.dtos.responses.PatientResponseDto;
import tn.iteam.meduserservice.dtos.responses.UserResponseDto;
import tn.iteam.meduserservice.models.DoctorEntity;
import tn.iteam.meduserservice.models.PatientEntity;
import tn.iteam.meduserservice.models.UserEntity;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface UserMapper {
    UserResponseDto toUserResponseDto(UserEntity entity);

    DoctorResponseDto toDoctorResponseDto(DoctorEntity entity);

    PatientResponseDto toPatientResponseDto(PatientEntity entity);
}
