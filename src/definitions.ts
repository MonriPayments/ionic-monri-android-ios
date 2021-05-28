export interface IonicMonriPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
