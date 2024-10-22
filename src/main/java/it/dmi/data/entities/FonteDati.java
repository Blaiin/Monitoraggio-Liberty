package it.dmi.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"MON_FonteDati\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FonteDati {

    @Id
    @Column(name = "\"FonteDatiID\"")
    private Long id;

    @Column(name = "\"Descrizione\"")
    private String descrizione;

    @Column(name = "\"NomeDriver\"")
    private String nomeDriver;

    @Column(name = "\"NomeClasse\"")
    private String nomeClasse;

    @Column(name = "\"URL\"")
    private String url;

    @Column(name = "\"JNDIName\"")
    private String jndiName;

}

