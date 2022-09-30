package me.dio.sacolaapi.service.impl;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.dio.sacolaapi.enumeration.FormaPagamento;
import me.dio.sacolaapi.model.Item;
import me.dio.sacolaapi.model.Restaurante;
import me.dio.sacolaapi.model.Sacola;
import me.dio.sacolaapi.repository.ItemRepository;
import me.dio.sacolaapi.repository.ProdutoRepository;
import me.dio.sacolaapi.repository.SacolaRepository;
import me.dio.sacolaapi.resource.dto.ItemDto;
import me.dio.sacolaapi.service.SacolaService;

@RequiredArgsConstructor
@Service
public class SacolaServiceImpl implements SacolaService {
    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemRepository itemRepository;

    @Override
    public Item incluirItemNaSacola(ItemDto itemDto) {
        Sacola sacola = verSacola(itemDto.getIdSacola());
        Boolean fechada = sacola.getFechada();
        if (fechada) {
            throw new RuntimeException("Esta Sacola está fechada!");
        }

        Item itemParaSerInserido = Item.builder()
                .quantidade(itemDto.getQuantidade())
                .sacola(sacola)
                .produto(produtoRepository.findById(itemDto.getProdutoId()).orElseThrow(
                        () -> {
                            throw new RuntimeException("Produto não encontrado");
                        }))
                .build();

        List<Item> itemsDaSacola = sacola.getItems();
        if (itemsDaSacola.isEmpty()) {
            itemsDaSacola.add(itemParaSerInserido);
        } else {
            Restaurante restauranteAtual = itemsDaSacola.get(0).getProduto().getRestaurante();
            Restaurante restauranteDoItemParaAdd = itemParaSerInserido.getProduto().getRestaurante();
            if (restauranteAtual.equals(restauranteDoItemParaAdd)) {
                itemsDaSacola.add(itemParaSerInserido);
            } else {
                throw new RuntimeException("Não é possivel add Produtos de restaurantes diferentes!");
            }
        }
        List<Double> valorDosItens = new ArrayList<>();
        for (Item itemDaSacola : itemsDaSacola) {
            double valorTotalItem = itemDaSacola.getProduto().getValorUnitario() * itemDaSacola.getQuantidade();
            valorDosItens.add(valorTotalItem);
        }
        double valorTotalSacola = valorDosItens.stream().mapToDouble(valortotalDeCadaItem -> valortotalDeCadaItem)
                .sum();
        /*
         * Double valorTotalSacola = 0.0;
         * for (Double valorItens : valorDosItens) {
         * valorTotalSacola += valorItens;
         * }
         */
        sacola.setValorTotal(valorTotalSacola);
        sacolaRepository.save(sacola);
        return itemRepository.save(itemParaSerInserido);
    }

    @Override
    public Sacola verSacola(Long id) {
        return sacolaRepository.findById(id).orElseThrow(
                () -> {
                    throw new RuntimeException("Could not find Sacola!");
                });
    }

    @Override
    public Sacola fecharSacola(Long id, int numeroFormaPagamento) {
        Sacola sacola = verSacola(id);
        if (sacola.getItems().isEmpty()) {
            throw new RuntimeException("Incluir itens na sacola!");
        }

        FormaPagamento formaPagamento = numeroFormaPagamento == 0 ? FormaPagamento.DINHEIRO : FormaPagamento.MAQUINETA;

        sacola.setFormaPagamento(formaPagamento);
        sacola.setFechada(true);
        return sacolaRepository.save(sacola);
    }

}
