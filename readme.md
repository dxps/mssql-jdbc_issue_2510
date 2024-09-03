## Demo project for `mssql-jdbc` issue 2510

This is a small demo project, done while helping with the investigation of `mssql-jdbc`'s [issue 2510](https://github.com/microsoft/mssql-jdbc/issues/2510).

<br/>

### Table schema

Considering such table:
```sql
CREATE TABLE user_tokens (
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

Having the previous table and entry manually created, 
to run the project either use:
- A custom profile (that should be defined in a dedicated `application-{someProfile}.properties` file).<br/>
  Ex: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`<br/>
- The default profile, but update the corresponding settings for url, username, and password.<br/>
  Ex: `mvn spring-boot:run`

Then going to its [Swagger UI](http://localhost:8080/swagger-ui/index.html#/token-controller/validate), 
you can call the `/tokens/validate` endpoint:
- through the browser
- or through command line like this:<br/>
  `curl -X POST http://localhost:8080/tokens/validate -H "Content-Type: application/json" -d '{ "token": "someToken" }'`

And in its output you'll see:
```
2024-09-03T17:33:24.372+03:00  INFO 1813529 --- [mssql_jdbc_issue_2510] [nio-8080-exec-8] o.d.m.controllers.TokenController        : Using find1, the token was not found.
2024-09-03T17:33:24.396+03:00  INFO 1813529 --- [mssql_jdbc_issue_2510] [nio-8080-exec-8] o.d.m.controllers.TokenController        : Using find2, the token was  found.
```
which demonstrates that:
- `find1` does not return the entry.<br/>
  This method uses the classic way of using a PreparedStatement with a parameter
- `find2` does return the row.<br/>
  This method avoids using a PreparedStatement with a named parameter by manually constructing the query statement before executing it