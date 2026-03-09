package mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import dto.response.KendaraanResponse;
import entity.Kendaraan;

@Mapper(componentModel = "cdi",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KendaraanMapper {

    @Mapping(source = "jenisKendaraan.id", target = "jenisKendaraanId")
    @Mapping(source = "driver.id", target = "driverId")
    @Mapping(source = "status", target = "status")
    KendaraanResponse toResponse(Kendaraan entity);
}

