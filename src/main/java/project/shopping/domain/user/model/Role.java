package project.shopping.domain.user.model;

public enum Role {
    CUSTOMER, SELLER;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}