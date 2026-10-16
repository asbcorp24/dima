import { CrawlerCommand, CrawlerTelemetry } from "../types/crawler";
import { CrawlerTransport } from "./CrawlerTransport";

const initialTelemetry: CrawlerTelemetry = {
  serialNumber: "ALTS-CR-0001",
  name: "Crawler 01",
  state: "STOP",
  direction: null,
  speedPercent: 0,
  distanceMeters: 128.4,
  totalMileageKm: 1427,
  rollDeg: 1.8,
  pitchDeg: -0.7,
  brakePercent: 72,
  magneticSignalPercent: 86,
  bleRssiDbm: -52,
  driveBattery: { voltage: 51.8, current: 3.2, temperature: 29, soc: 84 },
  xrayBattery: { voltage: 48.6, current: 1.1, temperature: 31, soc: 67 },
  sensors: { pipeStart: false, pipeEnd: false, water: false, obstacle: false },
  xrayState: "READY",
  xrayKv: 180,
  xrayMa: 4.5,
  xrayRemainingSec: 0,
  connected: true,
  lastUpdatedAt: Date.now()
};

/**
 * Симулятор контроллера для разработки до передачи окончательного BLE-протокола.
 */
export class MockCrawlerTransport implements CrawlerTransport {
  private telemetry: CrawlerTelemetry = initialTelemetry;
  private listeners = new Set<(value: CrawlerTelemetry) => void>();
  private timer: ReturnType<typeof setInterval> | null = null;

  async connect() {
    this.telemetry = { ...this.telemetry, connected: true, lastUpdatedAt: Date.now() };
    this.startSimulation();
    this.emit();
  }

  async disconnect() {
    this.telemetry = { ...this.telemetry, connected: false, lastUpdatedAt: Date.now() };
    if (this.timer) clearInterval(this.timer);
    this.timer = null;
    this.emit();
  }

  subscribe(listener: (telemetry: CrawlerTelemetry) => void) {
    this.listeners.add(listener);
    listener(this.telemetry);
    return () => this.listeners.delete(listener);
  }

  async send(command: CrawlerCommand) {
    // В реальном BLE-транспорте здесь будет ожидание ACK контроллера.
    switch (command.type) {
      case "MANUAL_MOVE_START":
        this.telemetry = {
          ...this.telemetry,
          state: "MOVING",
          direction: command.direction,
          speedPercent: 28
        };
        break;
      case "MANUAL_MOVE_STOP":
      case "STOP":
        this.telemetry = {
          ...this.telemetry,
          state: "STOP",
          direction: null,
          speedPercent: 0
        };
        break;
      case "AUTO_MOVE_START":
        this.telemetry = {
          ...this.telemetry,
          state: "MOVING",
          direction: command.direction,
          speedPercent: 55
        };
        break;
      case "SET_STATE":
        this.telemetry = {
          ...this.telemetry,
          state: command.state,
          direction: null,
          speedPercent: 0
        };
        break;
    }

    this.telemetry = { ...this.telemetry, lastUpdatedAt: Date.now() };
    this.emit();
  }

  private startSimulation() {
    if (this.timer) return;

    this.timer = setInterval(() => {
      const moving = this.telemetry.state === "MOVING";
      const sign = this.telemetry.direction === "BACKWARD" ? -1 : 1;
      const nextDistance = Math.max(
        0,
        this.telemetry.distanceMeters + (moving ? sign * 0.2 : 0)
      );

      this.telemetry = {
        ...this.telemetry,
        distanceMeters: Number(nextDistance.toFixed(1)),
        rollDeg: Number((this.telemetry.rollDeg + (Math.random() - 0.5) * 0.2).toFixed(1)),
        pitchDeg: Number((this.telemetry.pitchDeg + (Math.random() - 0.5) * 0.2).toFixed(1)),
        bleRssiDbm: Math.round(-50 - Math.random() * 8),
        lastUpdatedAt: Date.now()
      };
      this.emit();
    }, 1000);
  }

  private emit() {
    for (const listener of this.listeners) listener(this.telemetry);
  }
}
