package com.glow.user.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "customer_users")
public class CustomerUser extends User {

    // TODO: Should Address be as an embedded list or a separate entity?
    @ElementCollection
    public List<String> addresses;
}