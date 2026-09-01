const fs = require('fs');

// Lê o arquivo de log extraído do navegador
const logText = fs.readFileSync('web.bittrainers.com.br-1788273572551.log', 'utf-8');

// Regex para capturar o título e o tipo, tratando os dois formatos de saída do console
const regex = /'([^']+)'(?:'(DICA|RECEITA)'|,\s*tipo:\s*'(DICA|RECEITA)')/g;
const itensUnicos = new Map();
let match;

// Extrai e remove as duplicidades usando um Map
while ((match = regex.exec(logText)) !== null) {
    const nome = match[1].trim();
    const tipo = match[2] || match[3];

    if (!itensUnicos.has(nome)) {
        itensUnicos.set(nome, { nome, tipo });
    }
}

// Monta a estrutura final aguardada pelo ItemBatchImportDTO
const payload = {
    itens: Array.from(itensUnicos.values())
};

// Salva o JSON estruturado
fs.writeFileSync('payload-dicas.json', JSON.stringify(payload, null, 2));
console.log(`✅ Sucesso! ${payload.itens.length} itens únicos foram salvos em 'payload-dicas.json'.`);