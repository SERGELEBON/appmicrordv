package ci.hardwork.gestionrdvservice.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserReference {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isEnabled;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}