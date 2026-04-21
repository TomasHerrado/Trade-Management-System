package com.tsm.api.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SupplierRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String contactName;
    private String email;
    private String phone;
    private String address;
}
