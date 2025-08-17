package ci.hardwork.authservice.core.repository;

import ci.hardwork.authservice.core.models.User;
import ci.hardwork.authservice.core.models.UserHistory;
import ci.hardwork.authservice.core.models.enums.ActionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    List<UserHistory> findByUser(User user);
    List<UserHistory> findByUserAndAction(User user, ActionEnum action);
    List<UserHistory> findByActionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
