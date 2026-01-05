package com.workledger.core.timesheet.mapper;

import com.workledger.core.timesheet.domain.WorkEntry;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.dto.UpdateWorkEntryRequest;
import com.workledger.core.timesheet.dto.WorkEntryResponse;
import com.workledger.core.timesheet.dto.WorkEntrySummary;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for WorkEntry entity and its DTOs.
 * The generated class will be: WorkEntryMapperImpl
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface WorkEntryMapper {

    /**
     * Maps CreateWorkEntryRequest to WorkEntry Entity
     *
     * @param request the creation request DTO
     * @return new WorkEntry entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "workEntryStatus", target = "workEntryStatus", defaultValue = "DRAFT")
    WorkEntry toEntity(CreateWorkEntryRequest request);

    /**
     * Maps WorkEntry entity to WorkEntryResponse DTO
     *
     * @param workEntry the entity
     * @return response DTO with all fields
     */
    WorkEntryResponse toResponse(WorkEntry workEntry);

    /**
     * Maps WorkEntry entity to WorkEntrySummary DTO
     * Contains only essential fields for list views
     *
     * @param workEntry the entity
     * @return summary DTO
     */
    WorkEntrySummary toSummary(WorkEntry workEntry);

    /**
     * Updates existing WorkEntry entity from UpdateWorkEntryRequest.
     * Only updates non-null fields from the request
     *
     * @param request the update request DTO
     * @param workEntry the existing entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workEntryStatus",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateWorkEntryRequest request, @MappingTarget WorkEntry workEntry);

    /**
     * Maps a list of WorkEntry entities to WorkEntryResponse DTOs
     *
     * @param workEntries list of entities
     * @return list of response DTOs
     */
    List<WorkEntryResponse> toResponseList(List<WorkEntry> workEntries);

    /**
     * Maps a list of WorkEntry entities to WorkEntrySummary DTOs
     *
     * @param workEntries list of entities
     * @return list of summary DTOs
     */
    List<WorkEntrySummary> toSummaryList(List<WorkEntry> workEntries);
}
