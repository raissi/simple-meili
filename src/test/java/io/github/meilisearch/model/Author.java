package io.github.meilisearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

public class Author {
    private String uid;
    private String name;
    private String country;

    private String bio;



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

    public static class AuthorFormatted extends Author {
        @JsonProperty("_formatted")
        private Author formatted;

        public Author getFormatted() {
            return formatted;
        }

        public void setFormatted(Author formatted) {
            this.formatted = formatted;
        }
    }


    @Data
    public static class AuthorWithPositions extends AuthorFormatted {
        @JsonProperty("_matchesPosition")
        private Map<String, List<MatchPosition>> matchesPosition;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


}
