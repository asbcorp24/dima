import { CrawlerCommand, CrawlerTelemetry } from "../types/crawler";
import { CrawlerTransport } from "./CrawlerTransport";

/**
 * Заготовка реального BLE-транспорта.
 *
 * Намеренно не содержит выдуманных UUID и формата пакетов:
 * по ТЗ протокол ещё должен быть согласован с разработчиками Контроллера.
 * После утверждения протокола сюда будут добавлены:
 * - LE Secure Connections;
 * - Coded PHY S=8, если устройство/Android API это поддерживает;
 * - нумерация команд;
 * - ACK/повторы;
 * - CRC;
 * - подписка на телеметрию.
 */
export class BleCrawlerTransport implements CrawlerTransport {
  async connect(): Promise<void> {
    throw new Error("BLE protocol is not configured yet");
  }

  async disconnect(): Promise<void> {
    return;
  }

  async send(_command: CrawlerCommand): Promise<void> {
    throw new Error("BLE protocol is not configured yet");
  }

  subscribe(_listener: (telemetry: CrawlerTelemetry) => void): () => void {
    return () => undefined;
  }
}
