package cat.itacademy.barcelonactiva.mznmon.s05.t02.model.domain.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;
    private String email;
    @Column(unique = true, nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;



}