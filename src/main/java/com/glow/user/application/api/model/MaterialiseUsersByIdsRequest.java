package com.glow.user.application.api.model;

import java.util.List;

public record MaterialiseUsersByIdsRequest(List<String> ids) {
}