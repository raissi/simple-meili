package org.raissi.meilisearch.model;

public class Author {
    private String uid;
    private String name;
    private String country;

    public String getUid() {
        return uid;
    }

    public static class AuthorNoCountry {
        private String uid;
        private String name;

        private AuthorNoCountry() {
        }

        public AuthorNoCountry(Author author) {
            this.uid = author.getUid();
            this.name = author.getName();
        }

        public AuthorNoCountry(String uid, String name) {
            this.uid = uid;
            this.name = name;
        }

        public String getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
