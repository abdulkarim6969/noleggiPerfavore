package com.example.progettoinfonoleggi.repository.noleggi;

import com.example.progettoinfonoleggi.model.noleggi.Spedizioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpedizioniRepository extends JpaRepository<Spedizioni, Long> {

}
