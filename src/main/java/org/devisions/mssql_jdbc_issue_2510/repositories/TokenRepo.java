package org.devisions.mssql_jdbc_issue_2510.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devisions.mssql_jdbc_issue_2510.config.SQLServerDataSourceConfig;
import org.devisions.mssql_jdbc_issue_2510.model.TokenInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenRepo {

    @Autowired
    private SQLServerDataSourceConfig sqlServerDSConfig;

    private static final String findByUnhashedTokenQuery = "SELECT id, username FROM test.dbo.user_tokens WHERE token = hashbytes('SHA2_256', ?)";

    /**
     * `find1` is using the classic way of using a PreparedStatement with a parameter.
     */
    public Optional<TokenInfo> find1(String token) {

        Connection conn;
        try {
            conn = sqlServerDSConfig.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection to SQL Server", e);
        }

        log.info("[find1] Looking for token '{}' ...", token);

        try {
            var pstmt = conn.prepareStatement(findByUnhashedTokenQuery);
            pstmt.setString(1, token);
            pstmt.executeQuery();
            var rs = pstmt.getResultSet();

            Optional<TokenInfo> result;
            if (rs.next()) {
                var tokenInfo = new TokenInfo(rs.getInt("id"), rs.getString("username"));
                result = Optional.of(tokenInfo);
            } else {
                result = Optional.empty();
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute statement in SQL Server", e);
        }
    }

    /**
     * `find2` avoids using a PreparedStatement with a named parameter by manually constructing the query statement before executing it.
     */
    public Optional<TokenInfo> find2(String token) {

        Connection conn;
        try {
            conn = sqlServerDSConfig.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection to SQL Server", e);
        }

        log.info("[find2] Looking for token '{}' ...", token);

        var query = findByUnhashedTokenQuery.replace("?", String.format("'%s'", token));

        try {
            var rs = conn.prepareStatement(query).executeQuery();
            Optional<TokenInfo> result;
            if (rs.next()) {
                var tokenInfo = new TokenInfo(rs.getInt("id"), rs.getString("username"));
                result = Optional.of(tokenInfo);
            } else {
                result = Optional.empty();
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute statement in SQL Server", e);
        }
    }

}
