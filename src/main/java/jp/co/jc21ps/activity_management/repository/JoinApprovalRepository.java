package jp.co.jc21ps.activity_management.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import jp.co.jc21ps.activity_management.entity.JoinApprovalDataEntity;
import jp.co.jc21ps.activity_management.entity.JoinApprovalEntity;

@Repository
public class JoinApprovalRepository {
    private final JdbcTemplate jdbcTemplate;

    public JoinApprovalRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 初期画面表示
    public List<JoinApprovalEntity> getJoinApprovalList(JoinApprovalEntity paramEntity) {
        /*
         * TODO ➊ 初期表示情報を取得するSQLを完成させる。
         */
        String sql = """
                SELECT 
                    request.club_id,
                    request.user_id,
                    user.user_name,
                    club.club_name
                FROM 
                    trn_join_request request
                INNER JOIN
                    mst_user user ON request.user_id = user.user_id
                INNER JOIN
                    mst_club club ON request.club_id = club.club_id
                WHERE
                    request.club_id = ?
                """;

        List<JoinApprovalEntity> responseEntity = new ArrayList<>();
        
        try {
            List<Map<String, Object>> joinApprovalList = jdbcTemplate.queryForList(sql, paramEntity.getClubId());

            // リストが空だった場合
            if (joinApprovalList == null || joinApprovalList.isEmpty()) {
                return responseEntity;
            }

            for (Map<String, Object> joinApprovalLoop : joinApprovalList) {

                // entityに値をセット
                JoinApprovalEntity viewList = new JoinApprovalEntity();
                viewList.setClubId((String) joinApprovalLoop.get("club_id"));
                viewList.setUserId((String) joinApprovalLoop.get("user_id"));
                viewList.setUserName((String) joinApprovalLoop.get("user_name"));
                viewList.setClubName((String) joinApprovalLoop.get("club_name"));
                viewList.setLeaderFlg(false); // デフォルト値としてfalseを設定
                responseEntity.add(viewList);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseEntity;
        }

        return responseEntity;
    }

    // 部署名を表示
    public String getClubName(JoinApprovalEntity paramEntity) {
        String sql = """
                          SELECT
                               club_name
                           FROM
                mst_club
                           WHERE
                club_id = ?
                           """;

        try {
            List<Map<String, Object>> clubNameList = jdbcTemplate.queryForList(sql, paramEntity.getClubId());

            if (clubNameList == null || clubNameList.isEmpty()) {
                return "";
            }

            Map<String, Object> responseEntity = clubNameList.get(0);
            String clubName = (String) responseEntity.get("club_name");
            
            return (clubName != null) ? clubName : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // insert（承認）
    public void insertRequestInfo(JoinApprovalDataEntity paramEntity) {
        /*
         * TODO ➋ ユーザーを承認するSQL文を完成させる。
         */
        String sqlInsert = """
                INSERT INTO trn_club_member (club_id, user_id, leader_flg)
                VALUES (?, ?, ?)
                """;

        // entityから値をゲット
        Object[] paramList = {
                paramEntity.getClubId(),
                paramEntity.getUserId(),
                paramEntity.isLeaderFlg()
        };

        jdbcTemplate.update(sqlInsert, paramList);
    }

    // delete（否認）
    public void deleteRequestInfo(JoinApprovalDataEntity paramEntity) {
        /*
         * TODO ➌ ユーザーを否認するSQL文を完成させる。
         */
        String sqlDelete = """
                DELETE FROM trn_join_request
                WHERE user_id = ? AND club_id = ?
                """;

        // entityから値をゲット
        Object[] paramList = {
                paramEntity.getUserId(),
                paramEntity.getClubId()
        };

        jdbcTemplate.update(sqlDelete, paramList);
    }

}
