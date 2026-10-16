import { useEffect, useMemo, useState } from "react";
import { CrawlerTelemetry, Direction } from "../types/crawler";
import { MockCrawlerTransport } from "../services/MockCrawlerTransport";

const transport = new MockCrawlerTransport();

/**
 * Контроллер UI.
 * Финальное разрешение любой опасной команды всегда остаётся за бортовым Контроллером.
 */
export function useCrawlerController() {
  const [telemetry, setTelemetry] = useState<CrawlerTelemetry | null>(null);

  useEffect(() => {
    const unsubscribe = transport.subscribe(setTelemetry);
    void transport.connect();

    return () => {
      unsubscribe();
      void transport.disconnect();
    };
  }, []);

  return useMemo(
    () => ({
      telemetry,
      manualStart: (direction: Direction) =>
        transport.send({ type: "MANUAL_MOVE_START", direction }),
      manualStop: () => transport.send({ type: "MANUAL_MOVE_STOP" }),
      autoStart: (direction: Direction) =>
        transport.send({ type: "AUTO_MOVE_START", direction }),
      stop: () => transport.send({ type: "STOP" }),
      setStandby: () => transport.send({ type: "SET_STATE", state: "WAITING" }),
      setStop: () => transport.send({ type: "SET_STATE", state: "STOP" })
    }),
    [telemetry]
  );
}
