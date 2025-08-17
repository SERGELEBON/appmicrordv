package ci.hardwork.authservice.core.repository;

import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {
    List<UserAuthority> findByUser(User user);
    void deleteByUser(User user);
}
