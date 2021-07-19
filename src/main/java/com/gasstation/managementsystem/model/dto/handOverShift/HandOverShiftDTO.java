package com.gasstation.managementsystem.model.dto.handOverShift;

import com.gasstation.managementsystem.model.dto.pump.PumpDTO;
import com.gasstation.managementsystem.model.dto.shift.ShiftDTO;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class HandOverShiftDTO {
    private int id;
    private Long createdDate;
    private Long closeShiftDate;
    private String note;
    private ShiftDTO shift;
    private PumpDTO pump;

}
