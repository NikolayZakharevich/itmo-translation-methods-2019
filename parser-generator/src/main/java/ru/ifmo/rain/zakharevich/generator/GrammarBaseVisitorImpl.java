package ru.ifmo.rain.zakharevich.generator;

import ru.ifmo.rain.zakharevich.generator.parser.GrammarBaseVisitor;
import ru.ifmo.rain.zakharevich.generator.parser.GrammarParser;
import ru.ifmo.rain.zakharevich.util.Pair;

import static ru.ifmo.rain.zakharevich.generator.Grammar.*;


import java.util.List;
import java.util.stream.Collectors;

class GrammarBaseVisitorImpl extends GrammarBaseVisitor {

    String getName(GrammarParser.MetaGrammarContext root) {
        return root.name.getText();
    }

    String getHeader(GrammarParser.MetaGrammarContext root) {
        return root.header != null ? root.header.getText() : "";
    }

    String getFields(GrammarParser.MetaGrammarContext root) {
        return root.fields != null ? root.fields.getText() : null;
    }

    List<Terminal> getTerminals(List<GrammarParser.LexemRuleContext> lexemRules) {
        return lexemRules.stream().map(x -> {
            var name = x.name.getText();
            var options = x.lexemOptions().options.stream()
                    .map(y -> {
                        var wrapper = y.lexemTokenWrapper;
                        var pattern = wrapper.token.getText();
                        var isCharset = wrapper.token.charset != null;
                        var quantifier = wrapper.repeat != null ? wrapper.repeat.getText() : null;
                        var code = wrapper.code != null ? wrapper.code.getText() : null;
                        return new Pair<>(new Pattern(pattern, quantifier, isCharset), code);
                    })
                    .collect(Collectors.toList());
            var skip = x.skip != null;
            return new Terminal(name, options, skip);
        }).collect(Collectors.toList());
    }

    List<NonTerminal> getNonTerminals(List<GrammarParser.ProductionRuleContext> productionRules) {

        return productionRules.stream().map(rule -> new NonTerminal(
                rule.name.getText(),
                rule.productionOptions().options.stream()
                        .map(option -> {
                            var producing = option.wrappers.stream().map(w -> {
                                var args = w.token.args != null ? w.token.args.getText() : null;
                                if (w.token.lexemRuleName != null) {
                                    return new Terminal(w.token.lexemRuleName.getText());
                                }
                                if (w.token.parserRuleName != null) {
                                    return new NonTerminal(w.token.parserRuleName.getText(), args);
                                }
                                return new Symbol("");
                            }).collect(Collectors.toList());

                            if (producing.isEmpty()) {
                                producing.add(new Terminal(EPSILON));
                            }
                            var code = option.code != null ? option.code.getText() : null;
                            return new Rule(producing, code);
                        }).collect(Collectors.toList()),
                rule.args != null ? rule.args.getText() : NO_ARGUMENT)

        ).collect(Collectors.toList());
    }

}
