import { CrawlerCommand, CrawlerTelemetry } from "../types/crawler";

/**
 * Единая граница обмена с бортовым контроллером.
 * UI не должен знать, работает ли приложение через mock, BLE или стенд.
 */
export interface CrawlerTransport {
  connect(): Promise<void>;
  disconnect(): Promise<void>;
  send(command: CrawlerCommand): Promise<void>;
  subscribe(listener: (telemetry: CrawlerTelemetry) => void): () => void;
}
