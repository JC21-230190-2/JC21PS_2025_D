package jp.co.jc21ps.activity_management.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.Map;
import jp.co.jc21ps.activity_management.entity.ClubInfoRegisterEntity;

@Repository
public class ClubInfoRegisterRepository {
    private final JdbcTemplate jdbcTemplate;

    public ClubInfoRegisterRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 初期画面表示
    public ClubInfoRegisterEntity getClubInfo(ClubInfoRegisterEntity paramEntity) {
        /*
         * 初期表示情報を取得するSQL
         * mst_club テーブルから club_name と club_description を取得
         * club_id で条件を指定
         */
        //mst_club テーブルから club_name と club_description を取得
        //club_id = ? で条件を指定（paramEntity.getLeaderClubId() を使用）
        //パラメータとして leaderClubId を1回使用
        String sql = """
                SELECT
                    club_name,
                    club_description
                FROM
                    mst_club
                WHERE
                    club_id = ?
                """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, paramEntity.getLeaderClubId());

        // entityに値をセット
        ClubInfoRegisterEntity responseEntity = new ClubInfoRegisterEntity();
        responseEntity.setClubName((String) result.get("club_name"));
        responseEntity.setClubDescription((String) result.get("club_description"));

        return responseEntity;
    }

    // 活動説明更新
    public void updateClubInfo(ClubInfoRegisterEntity paramEntity) {
        /*
         * 部署情報をUPDATEするSQL
         * mst_club テーブルの club_description を更新
         * club_id で条件を指定
         */
        //mst_club テーブルの club_description を更新
        //club_id = ? で条件を指定（paramEntity.getLeaderClubId() を使用）
        //パラメータ は clubDescription と leaderClubId の順（60-63行目で定義されている通り）
        String sql = """
                UPDATE
                    mst_club
                SET
                    club_description = ?
                WHERE
                    club_id = ?
                """;

        // entityから値をget
        Object[] paramList = {
                paramEntity.getClubDescription(),
                paramEntity.getLeaderClubId()
        };

        jdbcTemplate.update(sql, paramList);
    }

}
