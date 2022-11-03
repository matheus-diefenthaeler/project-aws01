package br.com.diefenthaeler.matheus.aws_project01.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"invoiceNumber"})
})
@Entity
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, nullable = false)
    private String invoiceNumber;

    @Column(length = 32, nullable = false)
    private String customerName;

    private float totalValue;

    private long productId;

    private int quantity;

}


