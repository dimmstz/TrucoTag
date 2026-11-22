# TrucoTag

Marcador de pontuação para partidas de Truco, desenvolvido em Kotlin com Jetpack Compose.

## Recursos
- Placar independente para **Nós** e **Eles**.
- Adicionar e retirar pontos com controles dedicados.
- Mãos de 12, 15, 18, 21 ou 24 pontos.
- Persistência do placar e da mão selecionada no aparelho.
- Histórico de partidas concluídas durante a sessão.
- Interface escura inspirada em feltro verde e detalhes dourados.

## Identificação
- Aplicativo: TrucoTag
- Package: `com.dimm.trucotag`
- Versão inicial: 1.0.0
- Android mínimo: 6.0 (API 23)

## Compilar localmente
É necessário JDK 17 e Android SDK (API 35).

```bash
gradle assembleDebug
gradle assembleRelease
gradle bundleRelease
```

Os artefatos são gerados em `app/build/outputs/apk/` e `app/build/outputs/bundle/`.

## Licença
Apache License 2.0.
