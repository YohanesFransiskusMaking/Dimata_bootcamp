package mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import dto.request.UserRequest;
import dto.response.UserResponse;
import entity.AppUser;

@Mapper(componentModel = "cdi",
 unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface AppUserMapper {

    AppUser toEntity(UserRequest request);

    @Mapping(target = "isEmailVerified", source = "emailVerified")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponse toResponse(AppUser entity);
}
