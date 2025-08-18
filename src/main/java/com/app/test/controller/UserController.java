package com.app.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.test.exception.ResourceNotFoundException;
import com.app.test.model.user.*;
import com.app.test.service.UserService;
import com.app.test.specification.common.PageableUtil;
import com.app.test.specification.user.UserSpecificationBuilder;

import java.util.Optional;


@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Manage Users")
@Slf4j
public class UserController {

    private static final String ID = "userId";
    private static final String ITEM_NAME = "User";
    private static final String NEW_ITEM_LOG = "New " + ITEM_NAME + " was created id:{}";
    private static final String ITEM_UPDATED_LOG = ITEM_NAME + ": {} was updated";

    @Autowired
    UserService userService;

    // Get all users
    @Operation(summary = "Get all " + ITEM_NAME + "s and sorted/filtered based on the query parameters", operationId = "getAll" + ITEM_NAME + "Get")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all " + ITEM_NAME + "s", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserPageDto.class))}),
            @ApiResponse(responseCode = "404", description = ITEM_NAME + " not found", content = @Content)
    })
    @GetMapping
    public ResponseEntity<UserPageDto> getAll(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "lastname", required = false) String lastname,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "showTotalCount", required = false, defaultValue = "false") Boolean showTotalCount

    ) {

        UserSpecificationBuilder specificationBuilder = new UserSpecificationBuilder();
        Pageable pageable = PageableUtil.buildPageable(page, size, sort, direction);
        specificationBuilder
                .like(UserService.Fields.username.name(), username, false)
                .like(UserService.Fields.name.name(), name, false)
                .like(UserService.Fields.lastname.name(), lastname, false)
                .like(UserService.Fields.email.name(), email, false);
        UserPageDto pageDto = userService.getAll(specificationBuilder, pageable, showTotalCount);
        return ResponseEntity.ok(pageDto);
    }

    // Get user by id
    @Operation(summary = "Get a " + ITEM_NAME + " by its id", operationId = "get" + ITEM_NAME + "Get")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the " + ITEM_NAME, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "404", description = ITEM_NAME + " not found", content = @Content)
    })
    @GetMapping("/{" + ID + "}")
    public ResponseEntity<UserDto> get(@PathVariable(value = ID) Integer id) {
        Optional<UserDto> userDtoOptional = userService.get(id);
        if (!userDtoOptional.isPresent()) {
            throw new ResourceNotFoundException(User.class, id.toString());
        }
        return ResponseEntity.ok(userDtoOptional.get());
    }

    // Create user
    @Operation(summary = "Create a new " + ITEM_NAME, operationId = "create" + ITEM_NAME + "Post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = ITEM_NAME + " was created", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))})
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto user) {
        UserDto userDto = userService.create(user);
        log.trace(NEW_ITEM_LOG, userDto.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    // Update user
    @Operation(summary = "Update an " + ITEM_NAME + " by its id", operationId = "update" + ITEM_NAME + "Put")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = ITEM_NAME + " was updated", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "404", description = ITEM_NAME + " not found", content = @Content)
    })
    @PutMapping(path = "/{" + ID + "}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(@PathVariable(value = ID) Integer id, @RequestBody UserUpdateDto updateDto) {
        Optional<UserDto> optionalUserDto = userService.update(id, updateDto);
        // Log the updated user if it exists
        optionalUserDto.ifPresent(userDto -> log.info(ITEM_UPDATED_LOG, userDto));
        return optionalUserDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete user
    @Operation(summary = "Delete a " + ITEM_NAME + " by its id", operationId = "delete" + ITEM_NAME + "Delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = ITEM_NAME + " was deleted", content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "404", description = ITEM_NAME + " not found", content = @Content)
    })
    @DeleteMapping("/{" + ID + "}")
    public ResponseEntity<?> delete(@PathVariable(value = ID) Integer id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
