package jp.co.jc21ps.activity_management.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jp.co.jc21ps.activity_management.entity.JoinRequestEntity;
import jp.co.jc21ps.activity_management.entity.JoinRequestSaveEntity;

@Repository
public class JoinRequestRepository {
    private final JdbcTemplate jdbcTemplate;

    public JoinRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 初期画面表示
    public List<JoinRequestEntity> getJoinRequestById(JoinRequestEntity paramEntity) {
        String sql = """
            SELECT club_id, club_name, club_description
            FROM mst_club
            WHERE club_id NOT IN (
                SELECT club_id FROM trn_club_member WHERE user_id = ?
            )
            AND club_id NOT IN (
                SELECT club_id FROM trn_join_request WHERE user_id = ?
            )
            """;

        List<JoinRequestEntity> responseEntity = new ArrayList<>();
        List<Map<String, Object>> joinRequestList = jdbcTemplate.queryForList(sql, paramEntity.getUserId(),
                paramEntity.getUserId());

        if (joinRequestList.isEmpty()) {
            return responseEntity;
        }

        for (Map<String, Object> joinRequest : joinRequestList) {
            JoinRequestEntity joinData = new JoinRequestEntity();
            joinData.setClubName((String) joinRequest.get("club_name"));
            joinData.setClubDescription((String) joinRequest.get("club_description"));
            joinData.setClubId((String) joinRequest.get("club_id"));
            responseEntity.add(joinData);
        }

        return responseEntity;
    }

    // 申請処理
    public void insertClub(JoinRequestSaveEntity paramEntity) {
        String sql = """
            INSERT INTO trn_join_request (user_id, club_id) VALUES (?, ?)
            """;

        Object[] paramList = {
                paramEntity.getUserId(),
                paramEntity.getClubId(),
        };

        jdbcTemplate.update(sql, paramList);
    }
}
