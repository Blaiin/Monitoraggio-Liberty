package it.sogei.structure.data;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Config {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false)
        Long id;

        @Column(name = "query")
        String query;

        @Column(name = "query_type")
        String queryType;

        @Column(name = "query_description")
        String queryDescription;

        @Column(name = "target_db")
        String targetDb;

        @Column(name = "target_table")
        String targetTable;

        @Column(name = "expected_result")
        String expectedResult;
}
