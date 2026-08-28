package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ExampleRepositoryTests {

    @Autowired
    private ExampleRepository exampleRepository;

    @Test
    void savesAndLoadsExampleEntity() {
        final ExampleEntity saved = exampleRepository.save(ExampleEntity.create("demo"));

        final ExampleEntity found = exampleRepository.findById(saved.getId()).orElseThrow();

        assertThat(found.getName()).isEqualTo("demo");
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> ExampleEntity.create(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be blank");
    }
}
