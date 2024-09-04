## Demo project for `mssql-jdbc` issue 2510

This is a small demo project, done while helping with the investigation of `mssql-jdbc`'s [issue 2510](https://github.com/microsoft/mssql-jdbc/issues/2510).

<br/>

### Table schema

Considering such table:

```sql
CREATE TABLE test.dbo.user_tokens (
    id       int           IDENTITY(1,1)                        NOT NULL,
    username varchar(77)   COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
    token    varbinary(40) NULL
);
```

and adding an entry such as:

```sql
INSERT INTO test.dbo.user_tokens(username, token) VALUES ('dxps', hashbytes('SHA2_256', 'someToken'));
```

validating a token can be done like this:

```sql
SELECT id, username from test.dbo.user_tokens where token = hashbytes('SHA2_256', 'someToken');
```

### Usage

Having the previous table and entry manually created, to run the project either use:

-   A custom profile (that should be defined in a dedicated `application-{someProfile}.properties` file.<br/>
    and run it using `mvn spring-boot:run -Dspring-boot.run.profiles=dev`<br/><br/>
-   The default profile, but update the corresponding settings for url, username, and password,<br/>
    and run it using `mvn spring-boot:run`

Then you can access it using:

-   its [Swagger UI](http://localhost:8080/swagger-ui/index.html#/token-controller/validate), and call the `/tokens/validate` endpoint through the browser.
-   a CLI API client like this:<br/>
    `curl -X POST http://localhost:8080/tokens/validate -H "Content-Type: application/json" -d '{ "token": "someToken" }'`

And in its output you'll see:

```
2024-09-04T10:54:12.773+03:00  INFO 441719 --- [mssql_jdbc_issue_2510] [nio-8080-exec-1] o.d.m.repositories.TokenRepo             : [find1] Looking for token 'someToken' ...
2024-09-04T10:54:12.790+03:00  INFO 441719 --- [mssql_jdbc_issue_2510] [nio-8080-exec-1] o.d.m.controllers.TokenController        : [find1] The token was not found.
2024-09-04T10:54:12.793+03:00  INFO 441719 --- [mssql_jdbc_issue_2510] [nio-8080-exec-1] o.d.m.repositories.TokenRepo             : [find2] Looking for token 'someToken' ...
2024-09-04T10:54:12.799+03:00  INFO 441719 --- [mssql_jdbc_issue_2510] [nio-8080-exec-1] o.d.m.controllers.TokenController        : [find2] The token was found.
```

which demonstrates that:

-   `find1` does not return the row.<br/>
    This method uses the classic way of using a PreparedStatement with a parameter.
-   `find2` does return the row.<br/>
    This method avoids using a PreparedStatement with a named parameter by manually constructing the query statement before executing it.
