# Графовая база знаний

Графовая база знаний используется для описания коммуникативных сценариев и связанных с ними языковых единиц.

## Основные типы узлов

- CommunicativeScenario — коммуникативный сценарий;
- SituationStep — шаг сценария;
- Role — роль участника коммуникации;
- StructuralTemplate — структурный шаблон фразы;
- LexicalUnit — лексико-фразовая единица;
- CommunicativeIntent — коммуникативное намерение.

## Основные связи

- CommunicativeScenario → SituationStep
- SituationStep → StructuralTemplate
- StructuralTemplate → LexicalUnit
- StructuralTemplate → Role
- StructuralTemplate → CommunicativeIntent

## Назначение графовой модели

Графовая модель позволяет получать лексику по контексту употребления: сценарию, шагу сценария, роли говорящего и коммуникативному намерению.
