package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * SQL implementation for subarea repository
 */
final class SubAreaRepository implements fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository {
    private static class Loader implements RepositoryUtils.Loader<SubArea> {
        @Override
        public SubArea create(ResultSet rs) throws SQLException {
            return new SubArea(
                rs.getInt("SUBAREA_ID"),
                rs.getInt("AREA_ID"),
                rs.getString("SUBAREA_NAME"),
                rs.getBoolean("CONQUESTABLE"),
                Alignment.byId(rs.getInt("ALIGNMENT"))
            );
        }

        @Override
        public SubArea fillKeys(SubArea entity, ResultSet keys) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<SubArea> utils;

    public SubAreaRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);
        this.utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE SUBAREA (" +
                    "SUBAREA_ID INTEGER PRIMARY KEY," +
                    "AREA_ID INTEGER," +
                    "SUBAREA_NAME VARCHAR(200)," +
                    "CONQUESTABLE INTEGER(1)," +
                    "ALIGNMENT INTEGER(1)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE SUBAREA");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public SubArea get(SubArea entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM SUBAREA WHERE SUBAREA_ID = ?",
            rs -> rs.setInt(1, entity.id())
        );
    }

    @Override
    public boolean has(SubArea entity) {
        return utils.aggregate(
            "SELECT COUNT(*) FROM SUBAREA WHERE SUBAREA_ID = ?",
            rs -> rs.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<SubArea> all() {
        return utils.findAll("SELECT * FROM SUBAREA");
    }
}
