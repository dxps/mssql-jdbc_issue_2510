package org.devisions.mssql_jdbc_issue_2510.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devisions.mssql_jdbc_issue_2510.controllers.dto.ValidateTokenRequest;
import org.devisions.mssql_jdbc_issue_2510.model.TokenInfo;
import org.devisions.mssql_jdbc_issue_2510.repositories.TokenRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("tokens")
@RequiredArgsConstructor
@Slf4j
@Validated
public class TokenController {

    private final TokenRepo tokenRepo;

    @PostMapping("validate")
    public ResponseEntity<?> validate(@RequestBody @Valid ValidateTokenRequest request) {

        Optional<TokenInfo> optionalTokenInfo;

        optionalTokenInfo = tokenRepo.find1(request.token);
        log.info("[find1] The token was {}found.", optionalTokenInfo.isPresent() ? "" : "not ");

        optionalTokenInfo = tokenRepo.find2(request.token);
        log.info("[find2] The token was {}found.", optionalTokenInfo.isPresent() ? "" : "not ");

        if (optionalTokenInfo.isPresent()) {
            return ResponseEntity.ok(optionalTokenInfo.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "token not found");
        }
    }

}
