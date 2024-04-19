package com.rockbitegames.service;


import com.rockbitegames.domain.MaterialEntity;
import com.rockbitegames.domain.PlayerEntity;
import com.rockbitegames.dto.MaterialDto;
import com.rockbitegames.dto.PlayerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapperService {

    @Mapping(target = "observerManger", ignore = true)
    MaterialEntity toMaterialEntity(MaterialDto materialDto);


    @Mapping(target = "numberOfWarehouses", ignore = true)
    @Mapping(target = "warehouseEntityList", ignore = true)
    @Mapping(target = "observerManger", ignore = true)
    PlayerEntity toPlayerEntity(PlayerDto playerDto);
}
