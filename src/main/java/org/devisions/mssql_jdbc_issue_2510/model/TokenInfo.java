package org.devisions.mssql_jdbc_issue_2510.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenInfo {

    private Integer id;
    private String username;

}
