package io.pillopl.newmodel.catalogue;

import lombok.NonNull;
import lombok.Value;

import java.util.UUID;

@Value
public class BookId {

    @NonNull UUID uuid;
}
