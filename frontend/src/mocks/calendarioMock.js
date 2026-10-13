const calendarioMock = [
  { data: "2026-09-26", status: "APROVADO" },
  { data: "2026-09-27", status: "PENDENTE" },
];

export function reservarDataFake(data) {
  calendarioMock.push({ data, status: "PENDENTE" });
}

export function calendarioFake(mes, ano) {
  return calendarioMock.filter((item) => {
    const [anoItem, mesItem] = item.data.split("-").map(Number);
    return anoItem === ano && mesItem === mes;
  });
}
