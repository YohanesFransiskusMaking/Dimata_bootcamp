package mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import dto.response.DriverProfileResponse;
import dto.response.DriverAdminResponse;
import dto.response.DriverApplyStatusResponse;
import entity.DriverProfile;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface DriverProfileMapper {

    // ===== Pending Response =====
    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.nama", target = "nama")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.noHp", target = "noHp")
    @Mapping(source = "status", target = "status")
    DriverProfileResponse toResponse(DriverProfile entity);

    List<DriverProfileResponse> toResponseList(List<DriverProfile> entities);

    // ===== Admin Response =====
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nama", target = "nama")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.noHp", target = "noHp")
    @Mapping(source = "licenseNumber", target = "licenseNumber")
    @Mapping(source = "experienceYears", target = "experienceYears")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "rejectReason", target = "rejectReason")
    DriverAdminResponse toAdminResponse(DriverProfile entity);

    List<DriverAdminResponse> toAdminResponseList(List<DriverProfile> entities);


        // ===== Status Response =====
    @Mapping(target = "status", expression = "java(profile.getStatus().name())")
    @Mapping(source = "rejectReason", target = "rejectReason")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    DriverApplyStatusResponse toStatusResponse(DriverProfile profile);
}

