#!/usr/bin/env bash
# Gera docs/documentacao.pdf (padrão ABNT) a partir de docs/documentacao.md,
# usando Pandoc + LaTeX com a classe abnTeX2.
#
# Dependências no Ubuntu:
#   sudo apt install -y pandoc texlive-latex-extra texlive-publishers \
#                       texlive-lang-portuguese texlive-fonts-recommended
set -euo pipefail

cd "$(dirname "$0")"

pandoc documentacao.md \
    --output documentacao.pdf \
    --pdf-engine=pdflatex \
    --top-level-division=chapter \
    --resource-path=.

echo "PDF gerado: docs/documentacao.pdf"
